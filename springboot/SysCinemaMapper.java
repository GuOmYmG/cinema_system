package com.shanzhu.cm.mapper;

import com.shanzhu.cm.domain.SysCinema;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 影院持久层
 *
 * @author: ShanZhu
 * @date: 2023-12-11
 */
@Mapper
public interface SysCinemaMapper {

    SysCinema findCinema();

    int updateCinema(SysCinema sysCinema);

    SysCinema findCinemaById(Long id);

}
