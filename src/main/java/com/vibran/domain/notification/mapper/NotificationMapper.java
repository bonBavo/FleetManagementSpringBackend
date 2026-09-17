package com.vibran.domain.notification.mapper;

import com.vibran.domain.notification.dto.response.NotificationResponse;
import com.vibran.domain.notification.entity.Notification;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NotificationMapper {

    @Mapping(target = "alertId", source = "alert.id")
    NotificationResponse toResponse(Notification notification);
}