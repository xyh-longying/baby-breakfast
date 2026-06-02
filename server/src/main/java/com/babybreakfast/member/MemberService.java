package com.babybreakfast.member;

import com.babybreakfast.member.dto.CreateMemberRequest;
import com.babybreakfast.member.dto.MemberDTO;
import com.babybreakfast.member.dto.UpdateMemberRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final ConcurrentHashMap<Long, MemberDTO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private static final Long DEFAULT_FAMILY_ID = 1L;

    public MemberService() {
        MemberDTO parent = new MemberDTO(
                101L, DEFAULT_FAMILY_ID, "凌云", "parent", "female", "1992-01-15", null,
                List.of("海鲜"), List.of("低盐"), List.of("喜欢面食")
        );
        MemberDTO child = new MemberDTO(
                102L, DEFAULT_FAMILY_ID, "汤圆", "child", "female", "2020-03-20", null,
                List.of("芒果", "花生"), List.of(), List.of("喜欢甜食", "不喜欢胡萝卜")
        );
        store.put(101L, parent);
        store.put(102L, child);
        idGenerator.set(103);
    }

    public List<MemberDTO> findAll() {
        return new ArrayList<>(store.values());
    }

    public MemberDTO findById(Long id) {
        return store.get(id);
    }

    public MemberDTO create(CreateMemberRequest request) {
        Long id = idGenerator.getAndIncrement();
        MemberDTO member = new MemberDTO(
                id, DEFAULT_FAMILY_ID, request.name(), request.role(), request.gender(), request.birthday(),
                request.avatarUrl(), request.allergens(), request.dietaryRestrictions(), request.preferences()
        );
        store.put(id, member);
        return member;
    }

    public MemberDTO update(Long id, UpdateMemberRequest request) {
        MemberDTO existing = store.get(id);
        if (existing == null) {
            throw new IllegalArgumentException("Member not found: " + id);
        }
        MemberDTO updated = new MemberDTO(
                id,
                existing.familyId(),
                request.name() != null ? request.name() : existing.name(),
                request.role() != null ? request.role() : existing.role(),
                request.gender() != null ? request.gender() : existing.gender(),
                request.birthday() != null ? request.birthday() : existing.birthday(),
                request.avatarUrl() != null ? request.avatarUrl() : existing.avatarUrl(),
                request.allergens() != null ? request.allergens() : existing.allergens(),
                request.dietaryRestrictions() != null ? request.dietaryRestrictions() : existing.dietaryRestrictions(),
                request.preferences() != null ? request.preferences() : existing.preferences()
        );
        store.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}
