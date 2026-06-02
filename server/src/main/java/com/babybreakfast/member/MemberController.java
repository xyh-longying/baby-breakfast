package com.babybreakfast.member;

import com.babybreakfast.common.ApiResponse;
import com.babybreakfast.member.dto.CreateMemberRequest;
import com.babybreakfast.member.dto.MemberDTO;
import com.babybreakfast.member.dto.UpdateMemberRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberDTO>>> listMembers() {
        List<MemberDTO> members = memberService.findAll();
        return ResponseEntity.ok(ApiResponse.success(members));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberDTO>> getMember(@PathVariable Long id) {
        MemberDTO member = memberService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(member));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberDTO>> createMember(@RequestBody CreateMemberRequest request) {
        MemberDTO member = memberService.create(request);
        return ResponseEntity.ok(ApiResponse.success(member));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberDTO>> updateMember(
            @PathVariable Long id,
            @RequestBody UpdateMemberRequest request
    ) {
        MemberDTO member = memberService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(member));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
