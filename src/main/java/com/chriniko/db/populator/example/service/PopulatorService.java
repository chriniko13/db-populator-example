package com.chriniko.db.populator.example.service;

import com.chriniko.db.populator.example.dao.TaskDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PopulatorService {


    private final TaskDAO taskDAO;

    @Autowired
    public PopulatorService(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRES_NEW
    )
    public void populateDB(int trafficTargetAsInt) {
        taskDAO.storeRandomRecords(trafficTargetAsInt);
    }

}
