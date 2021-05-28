package game.iquanzi.top.task;

import game.iquanzi.top.service.MessageService;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳任务<br/>
 * -----------------------------------------------------------------------------
 *
 * @author Mr.Z
 * @version 1.0
 * @date 2021-05-21
 * @since JDK 1.8
 */
@Slf4j
public class HeartBeatTask extends ScheduledService<Void> {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                log.info("执行心跳任务");
                //向服务端发送心跳消息--Ping
                MessageService.getInstance().sendHeartMsg();
                return null;
            }
        };
    }
}
