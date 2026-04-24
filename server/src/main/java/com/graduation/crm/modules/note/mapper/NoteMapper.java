package com.graduation.crm.modules.note.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.crm.modules.note.entity.Note;
import com.graduation.crm.modules.note.vo.NoteVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {

    NoteVO selectNoteDetail(@Param("id") Long id);

    List<NoteVO> selectByCustomerId(@Param("customerId") Long customerId);
}

