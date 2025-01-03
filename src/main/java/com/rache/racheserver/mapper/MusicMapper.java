package com.rache.racheserver.mapper;

import com.rache.racheserver.entity.MusicConfigEntity;

import java.util.List;

public interface MusicMapper {
    List<MusicConfigEntity> selectAll();
    List<MusicConfigEntity> selectById(int id);

    List<MusicConfigEntity> selectLimit(int limit, int start);

    List<MusicConfigEntity> selectAudioById(int id);
    List<MusicConfigEntity> selectCoverById(int id);

    Integer getLastInsertID();

    int insertConfig(String name, String singer, String album, boolean multiline, String description);

    int updateLyrics(int id, String lyrics);
    int updateAudio(int id, String audio);
    int updateCover(int id, String cover);
}
