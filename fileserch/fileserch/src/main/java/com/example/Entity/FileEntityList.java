package com.example.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class FileEntityList {
    /**
     * List 实体类对象
     */
    private List<FileEntity> Result;

    /**
     * 查询符合条件的条数
     */
    private Integer rowCount;

}
