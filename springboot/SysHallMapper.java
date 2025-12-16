package com.shanzhu.cm.mapper;

import com.shanzhu.cm.domain.SysHall;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 排座持久层
 *
 * @author: ShanZhu
 * @date: 2023-12-11
 */
@Mapper
public interface SysHallMapper {

    List<SysHall> findAllHalls(SysHall sysHall);

    SysHall findHallById(SysHall sysHall);

    int addHall(SysHall sysHall);

    int updateHall(SysHall sysHall);

    int deleteHall(SysHall sysHall);
}
