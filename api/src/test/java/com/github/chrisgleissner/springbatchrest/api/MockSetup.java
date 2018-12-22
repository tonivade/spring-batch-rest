package com.github.chrisgleissner.springbatchrest.api;

import com.github.chrisgleissner.springbatchrest.util.adhoc.AdHocStarter;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;

import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.batch.core.ExitStatus.COMPLETED;
import static org.springframework.batch.core.ExitStatus.FAILED;

public class MockSetup {

    private static final List<String> JOB_NAMES = newArrayList("j1", "j2");
    private static JobInstance j11 = new JobInstance(1L, "j1");
    private static JobInstance j12 = new JobInstance(2L, "j1");
    private static JobInstance j21 = new JobInstance(3L, "j2");
    private static JobInstance j22 = new JobInstance(4L, "j2");
    private static JobInstance j23 = new JobInstance(5L, "j2");

    public static void configureMock(JobExplorer jobExplorer) {
        reset(jobExplorer);
        when(jobExplorer.getJobNames()).thenReturn(JOB_NAMES);
    }

    public static void configureMock(JobRegistry jobRegistry) {
        reset(jobRegistry);
        when(jobRegistry.getJobNames()).thenReturn(JOB_NAMES);
    }

    public static void configureMock(AdHocStarter adHocStarter) {
        reset(adHocStarter);
        when(adHocStarter.start(any())).thenReturn(jobExecution(j11, 1, ExitStatus.EXECUTING));
    }

    public static void configureMockForJobExecutionsService(JobExplorer jobExplorer) throws NoSuchJobException {
        when(jobExplorer.getJobInstances(eq("j1"), anyInt(), anyInt())).thenReturn(newArrayList(j11, j12));
        when(jobExplorer.getJobInstances(eq("j2"), anyInt(), anyInt())).thenReturn(newArrayList(j21, j22, j23));

        when(jobExplorer.getJobExecutions(j11)).thenReturn(newArrayList(jobExecution(j11, 1, COMPLETED), jobExecution(j11, 2, FAILED)));
        when(jobExplorer.getJobExecutions(j12)).thenReturn(newArrayList(jobExecution(j12, 3, COMPLETED)));
        when(jobExplorer.getJobExecutions(j21)).thenReturn(newArrayList());
        when(jobExplorer.getJobExecutions(j22)).thenReturn(newArrayList(jobExecution(j21, 4, FAILED), jobExecution(j21, 5, FAILED)));
        when(jobExplorer.getJobExecutions(j23)).thenReturn(newArrayList(jobExecution(j23, 6, COMPLETED)));

        when(jobExplorer.getJobExecution(1L)).thenReturn(jobExecution(j11, 1, COMPLETED));
    }


    private static JobExecution jobExecution(JobInstance ji, int id, ExitStatus exitStatus) {
        JobExecution jobExecution = new JobExecution(ji, (long) id, null, "config" + id);
        jobExecution.setCreateTime(new Date(id * 100L));
        jobExecution.setStartTime(new Date(id * 200L));
        jobExecution.setEndTime(new Date(id * 300L));
        jobExecution.setExitStatus(exitStatus);
        jobExecution.setStatus(BatchStatus.COMPLETED);
        return jobExecution;
    }

    private static JobInstance jobInstance(long id, String jobName) {
        return new JobInstance(id, jobName);
    }
}
