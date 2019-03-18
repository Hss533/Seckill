package per.hss.exception;

import per.hss.dto.SeckillExecution;

/**
 * 重复秒杀异常
 * 运行期异常
 * spring 事物只接受运行期异常
 */
public class RepeatKillException extends SeckillException
{

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
