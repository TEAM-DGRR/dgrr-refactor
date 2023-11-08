package live.dgrr.domain.waitingroom.service;

import live.dgrr.domain.waitingroom.entity.MemberRoomMapping;
import live.dgrr.domain.waitingroom.repository.MemberRoomMappingRepository;
import live.dgrr.global.exception.ErrorCode;
import live.dgrr.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberRoomMappingService {

    private final MemberRoomMappingRepository memberRoomMappingRepository;

    public MemberRoomMapping findRoomIdByMemberId(long memberId) {
        return memberRoomMappingRepository.findById(memberId).orElseThrow(() -> new GeneralException(ErrorCode.MEMBER_ROOM_MAPPING_NOT_FOUND));
    }

}
