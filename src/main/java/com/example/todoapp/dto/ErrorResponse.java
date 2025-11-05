package com.example.todoapp.dto;

public record ErrorResponse(int statusCode, String message, long timestamp) { //immutability
}