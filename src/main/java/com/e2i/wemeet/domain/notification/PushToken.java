package com.e2i.wemeet.domain.notification;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PUSH_TOKEN")
@Entity
public class PushToken extends BaseTimeEntity implements Persistable<String> {

    @Id
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public PushToken(String token, Member member) {
        this.token = token;
        this.member = member;
    }

    public void setMember(final Member member) {
        this.member = member;
    }

    @Override
    public String getId() {
        return this.token;
    }

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
