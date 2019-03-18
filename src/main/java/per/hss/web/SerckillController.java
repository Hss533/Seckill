package per.hss.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import per.hss.dto.Exposer;
import per.hss.dto.SeckillExecution;
import per.hss.dto.SeckillResult;
import per.hss.entity.Seckill;
import per.hss.enums.SeckillStateEnum;
import per.hss.exception.RepeatKillException;
import per.hss.exception.SeckillCloseException;
import per.hss.exception.SeckillException;
import per.hss.service.SeckillService;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")
public class SerckillController
{
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;


    @RequestMapping(value="/list",method = RequestMethod.GET)
    public String list(Model model)
    {
        //list.jsp+model=ModelAndView
        //获取列表页
        List<Seckill> seckillList= seckillService.getSeckillList();

        model.addAttribute("list",seckillList);
        return "list";
    }

    @RequestMapping(value ="/{seckillId}/detial",method = RequestMethod.GET)
    public String detial(@PathVariable("seckillId") Long seckillId, Model model)
    {
        if(seckillId==null)
        {
            return "redirect:/seckill/list";//请求重定向
        }
        Seckill seckill=seckillService.getById(seckillId);
        if(seckill==null)
        {
            return "redirect:/seckill/list";
        }
        model.addAttribute("seckill",seckill) ;
        return "detail";
    }
    //ajax json 通过注解告诉这是一个json格式的
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(Long seckillId)
    {
        SeckillResult<Exposer> result;
        try
        {

            Exposer exposer=seckillService.exportSeckillUrl(seckillId);
            result=new SeckillResult<Exposer>(true,exposer);

        } catch (Exception e) {
            logger.error(e.getMessage( ),e);
            result=new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    //执行秒杀
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,  produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                    @PathVariable("md5") String md5,
                                                    @CookieValue(value = "killphone",required = false) Long phone)
    {
        if(phone == null)
        {
            return new SeckillResult<SeckillExecution>(false,"weizhuce");
        }
        SeckillResult<SeckillExecution> result;
        try
        {
            SeckillExecution execution=seckillService.executeSeckillProce(seckillId,phone,md5);
           return  new SeckillResult<SeckillExecution>(true,execution);

        }
        catch(RepeatKillException e)
        {
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(false,execution);
        }
        catch(SeckillCloseException e)
        {
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(false,execution);
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(false,execution);
        }
    }

    @RequestMapping(value = "/time/new" ,method = RequestMethod.GET)
    public SeckillResult<Long> time()
    {
        Date date=new Date();
        return new SeckillResult(true,date.getTime());
    }
}
