package com.example.quartzdemo.controller;

import com.example.quartzdemo.job.TestJob;
import com.example.quartzdemo.payload.ScheduleTestRequest;
import com.example.quartzdemo.payload.ScheduleTestResponse;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@RestController
public class MessageJobSchedulerController {
    private static final Logger logger = LoggerFactory.getLogger(MessageJobSchedulerController.class);

    @Autowired
    private Scheduler scheduler;

    @PostMapping("/scheduleMessage")
    public ResponseEntity<ScheduleTestResponse> createSchedule(@Valid @RequestBody ScheduleTestRequest scheduleTestRequest) {
        try {
            ZonedDateTime dateTime = ZonedDateTime.of(scheduleTestRequest.getDateTime(), scheduleTestRequest.getTimeZone());
            if(dateTime.isBefore(ZonedDateTime.now())) {
                ScheduleTestResponse scheduleTestResponse = new ScheduleTestResponse(false,
                        "dateTime must be after current time");
                return ResponseEntity.badRequest().body(scheduleTestResponse);
            }

            JobDetail jobDetail = buildJobDetail(scheduleTestRequest);
            Trigger trigger = buildJobTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);

            ScheduleTestResponse scheduleTestResponse = new ScheduleTestResponse(true,
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Message Scheduled Successfully!");
            return ResponseEntity.ok(scheduleTestResponse);
        } catch (SchedulerException ex) {
            logger.error("Error scheduling message", ex);

            ScheduleTestResponse scheduleTestResponse = new ScheduleTestResponse(false,
                    "Error scheduling message. Please try later!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(scheduleTestResponse);
        }
    }

    private JobDetail buildJobDetail(ScheduleTestRequest scheduleTestRequest) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("message", scheduleTestRequest.getMessage());

        return JobBuilder.newJob(TestJob.class)
                .withIdentity(UUID.randomUUID().toString(), "test-jobs")
                .withDescription("Send Message Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "test-triggers")
                .withDescription("Send Message Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
