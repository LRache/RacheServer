package com.rache.racheserver.mapper;

import com.rache.racheserver.entity.MusicConfigEntity;

import java.util.List;

public interface MusicMapper {
    List<MusicConfigEntity> selectAll();
    List<MusicConfigEntity> selectById(int id);
    List<MusicConfigEntity> selectAudioById(int id);
    List<MusicConfigEntity> selectCoverById(int id);

    Integer getLastInsertID();

    int insertConfig(String name, String singer, String album);

    int updateLyrics(int id, String lyrics);
    int updateAudio(int id, String audio);
    int updateCover(int id, String cover);
}
