package com.example.diplom.services;

import com.example.diplom.entities.Transfer;
import org.springframework.stereotype.Service;

@Service
public class FrodMonitoringService {

    public boolean isPermittedTransfer(Transfer transfer) {
        return transfer.getSum() < 100_000;
    }

}
