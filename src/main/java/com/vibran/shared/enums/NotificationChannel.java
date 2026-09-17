package com.vibran.shared.enums;

public enum NotificationChannel {
    PUSH,       // FCM — works when app is closed
    IN_APP,     // WebSocket — works when app is open
    SMS,        // future — Safaricom SMS API
    EMAIL       // future — SendGrid
}
