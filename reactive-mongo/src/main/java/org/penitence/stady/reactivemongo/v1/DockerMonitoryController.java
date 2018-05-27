package org.penitence.stady.reactivemongo.v1;

import org.penitence.stady.reactivemongo.dao.MonitoryCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/v1/monitors")
public class DockerMonitoryController {

    @Autowired
    private MonitoryCustomRepository repository;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @InitBinder
    protected void webInit(WebDataBinder binder) {

        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }

    @GetMapping("")
    public Mono<Page<Map>> findMonitorByPage(int page, int size) {
        Mono<Page<Map>> mono = repository.findByPage(page, size);
        return mono;
    }

    /**
     *
     * @param max 最大返回多少个点
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param type      监控的类型
     * @param dockerId 容器的id
     * @return 返回监控的历史数据
     */
    @GetMapping("/list")
    public Flux<Map> findMonitorsList(@RequestParam(defaultValue = "500") int max,
                                      @RequestParam Date startTime,
                                      Date endTime,
                                      String type,
                                      String dockerId) {
        return repository.findList(max, startTime, endTime, type, dockerId);
    }

}
