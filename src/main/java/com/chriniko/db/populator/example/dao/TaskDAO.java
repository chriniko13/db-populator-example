package com.chriniko.db.populator.example.dao;

import com.chriniko.db.populator.example.domain.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@Repository
public class TaskDAO {

    private static boolean SIMULATE_IO_ERROR = false;

    private final SessionFactory sessionFactory;

    @Autowired
    public TaskDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void storeRandomRecords(int howMany) {

        IntStream.rangeClosed(1, howMany)
                .forEach(idx -> {

                    Session currentSession = sessionFactory.getCurrentSession();

                    Task task = new Task(
                            UUID.randomUUID().toString(),
                            "Name ---" + idx,
                            ZonedDateTime.now().plusDays(idx).toString()
                    );

                    if (SIMULATE_IO_ERROR) {
                        if (new Random().nextInt(2) == 1) {
                            throw new IllegalStateException("db io error occurred.");
                        }
                    }


                    currentSession.save(task);

                });

    }

}
