/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.FileDirectoryEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sinhv
 */
@Stateless
public class FileDirectorySession implements FileDirectorySessionLocal {
    @PersistenceContext(unitName = "Qoodie-ejbPU")
    private EntityManager em;

    @Override
    public void createFile(FileDirectoryEntity fileDirectoryEntity) {
        em.persist(fileDirectoryEntity);
    }

    @Override
    public FileDirectoryEntity getFile(Long id) {
        return em.find(FileDirectoryEntity.class, id);
    }
}
