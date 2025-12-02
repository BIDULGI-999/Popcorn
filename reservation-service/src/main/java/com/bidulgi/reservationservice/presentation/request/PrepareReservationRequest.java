package com.bidulgi.reservationservice.presentation.request;

public record PrepareReservationRequest(
	String visitorName,
	String visitorPhone
) {}