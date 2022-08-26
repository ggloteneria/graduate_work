package com.example.diplom.services;

import com.example.diplom.entities.Transfer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransferServiceTest {

    @Autowired
    TransferService transferService;

    @Test
    void isSberMoneyStorage() {
        Transfer transfer = new Transfer();
        transfer.setNumberForTransfer("4276002128");
        assertTrue(transferService.isSberMoneyStorage(transfer));
    }
}