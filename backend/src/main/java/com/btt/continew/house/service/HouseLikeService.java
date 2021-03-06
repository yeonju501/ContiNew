package com.btt.continew.house.service;

import com.btt.continew.global.exception.BusinessException;
import com.btt.continew.global.exception.ErrorCode;
import com.btt.continew.house.controller.dto.response.HouseLikeResponse;
import com.btt.continew.house.controller.dto.response.HouseListResponse;
import com.btt.continew.house.domain.House;
import com.btt.continew.house.domain.HouseLike;
import com.btt.continew.house.domain.HouseLikeRepository;
import com.btt.continew.member.domain.Member;
import com.btt.continew.member.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HouseLikeService {

    private final HouseLikeRepository houseLikeRepository;
    private final HouseService houseService;
    private final MemberService memberService;

    public HouseLikeService(HouseLikeRepository houseLikeRepository, HouseService houseService,
        MemberService memberService) {
        this.houseLikeRepository = houseLikeRepository;
        this.houseService = houseService;
        this.memberService = memberService;
    }

    @Transactional
    public void create(Long houseId, String loginId) {
        House house = houseService.findById(houseId);
        Member member = memberService.findByLoginId(loginId);

        if (houseLikeRepository.existsByHouseAndMember(house, member)) {
            throw new BusinessException(ErrorCode.HOUSE_LIKE_ALREADY_EXISTS_BY_LOGINID);
        }

        HouseLike houseLike = HouseLike.builder()
            .house(house)
            .member(member)
            .build();
        houseLikeRepository.save(houseLike);
    }

    @Transactional(readOnly = true)
    public HouseListResponse show(String loginId, Pageable pageable) {
        Member member = memberService.findByLoginId(loginId);
        Page<HouseLike> houseLikes = houseLikeRepository.findAllByMember(member, pageable);
        return HouseListResponse.fromHouseLikes(houseLikes);
    }

    @Transactional(readOnly = true)
    public HouseLikeResponse checkLiked(String loginId, Long houseId) {
        Member member = memberService.findByLoginId(loginId);
        House house = houseService.findById(houseId);
        return new HouseLikeResponse(houseLikeRepository.existsByHouseAndMember(house, member));
    }

    @Transactional
    public void delete(Long houseId, String loginId) {
        House house = houseService.findById(houseId);
        Member member = memberService.findByLoginId(loginId);

        HouseLike houseLike = houseLikeRepository.findByHouseAndMember(house, member)
            .orElseThrow(() -> new BusinessException(ErrorCode.HOUSE_LIKE_NOT_FOUND_BY_LOGINID));

        houseLike.saveDeletedTime();
    }
}
