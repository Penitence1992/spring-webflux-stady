package org.penitence.stady.reactivepg.domain;

import lombok.Data;

import java.util.Date;

/**
 * 服务容器实例信息存放的表
 */
@Data
public class ServiceContainer {

    private String id; //使用taskId

    private String containerId;

    private String serviceTableId;

    private String containerName;

    private String containerImageName;

    private String status;

    private String nodeId;

    //用于存放任务所在的swarm节点,以便同步信息的时候可以根据这个参数进行筛选
    private String swarmId;

    private boolean del;

    private Date created = new Date();

    private Date updated = new Date();
}
