/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.FileDirectoryEntity;

import javax.ejb.Local;

/**
 *
 * @author sinhv
 */
@Local
public interface FileDirectorySessionLocal {
    void createFile(FileDirectoryEntity fileDirectoryEntity);
    FileDirectoryEntity getFile(Long id);
}
