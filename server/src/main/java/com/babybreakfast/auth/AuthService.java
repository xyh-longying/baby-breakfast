package com.babybreakfast.auth;

import com.babybreakfast.auth.dto.AuthResponse;
import com.babybreakfast.auth.dto.FamilyMemberResponse;
import com.babybreakfast.auth.dto.FamilyResponse;
import com.babybreakfast.auth.dto.LoginRequest;
import com.babybreakfast.auth.dto.SessionUserResponse;
import com.babybreakfast.auth.dto.SwitchRoleRequest;
import com.babybreakfast.member.MemberService;
import com.babybreakfast.member.dto.MemberDTO;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private final MemberService memberService;

    public AuthService(MemberService memberService) {
        this.memberService = memberService;
    }

    public AuthResponse login(LoginRequest request) {
        if (!ADMIN_USERNAME.equals(request.username()) || !ADMIN_PASSWORD.equals(request.password())) {
            throw new AuthException("用户名或密码错误");
        }
        List<MemberDTO> allMembers = memberService.findAll();
        MemberDTO member = allMembers.stream()
                .filter(m -> "parent".equals(m.role()))
                .findFirst()
                .orElse(allMembers.get(0));
        return buildSession(member);
    }

    public AuthResponse currentSession() {
        List<MemberDTO> allMembers = memberService.findAll();
        MemberDTO member = allMembers.stream()
                .filter(m -> "parent".equals(m.role()))
                .findFirst()
                .orElse(allMembers.get(0));
        return buildSession(member);
    }

    public AuthResponse switchRole(SwitchRoleRequest request) {
        List<MemberDTO> allMembers = memberService.findAll();
        MemberDTO member = allMembers.stream()
                .filter(m -> m.id().equals(request.memberId()))
                .findFirst()
                .orElseGet(() -> allMembers.stream()
                        .filter(m -> m.role().equals(request.role()))
                        .findFirst()
                        .orElse(allMembers.get(0)));
        return buildSession(member);
    }

    private AuthResponse buildSession(MemberDTO currentMember) {
        List<MemberDTO> allMembers = memberService.findAll();
        List<FamilyMemberResponse> memberSummaries = allMembers.stream()
                .map(FamilyMemberResponse::fromMemberDTO)
                .toList();
        return new AuthResponse(
                "token-" + System.currentTimeMillis(),
                new SessionUserResponse(1L, "admin", "管理员"),
                new FamilyResponse(1L, "默认家庭", memberSummaries),
                FamilyMemberResponse.fromMemberDTO(currentMember)
        );
    }
}
