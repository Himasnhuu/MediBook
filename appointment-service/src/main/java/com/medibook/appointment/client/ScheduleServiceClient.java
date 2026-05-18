package com.medibook.appointment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "SCHEDULE-SERVICE")
public interface ScheduleServiceClient {

    @PutMapping("/slots/{slotId}/book")
    String bookSlot(@PathVariable Long slotId);

    @PutMapping("/slots/{slotId}/release")
    String releaseSlot(@PathVariable Long slotId);
}
