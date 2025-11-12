package com.nhnacademy.byeol23backend.orderset.refundpolicy.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.orderset.refundpolicy.service.RefundPolicyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundPolicyServiceImpl implements RefundPolicyService {

}
