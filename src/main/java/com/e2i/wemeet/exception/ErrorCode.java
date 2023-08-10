package com.e2i.wemeet.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    HTTP_METHOD_NOT_ALLOWED(40000, "http.method.not.allowed"),

    INVALID_SMS_CREDENTIAL(40001, "invalid.sms.credential"),
    INVALID_GENDER_VALUE(40002, "invalid.gender.value"),
    INVALID_MBTI_VALUE(40003, "invalid.mbti.value"),

    INVALID_DATA_FORMAT(40004, "invalid.data.format"),
    INVALID_PHONE_FORMAT(40005, "invalid.phone.format"),
    INVALID_EMAIL_FORMAT(40006, "invalid.email.format"),
    INVALID_CREDENTIAL_FORMAT(40007, "invalid.credential.format"),
    DUPLICATED_PHONE_NUMBER(40008, "duplicated.phone.number"),
    DUPLICATED_MAIL(40009, "duplicated.mail"),
    METHOD_ARGUMENT_NOT_VALID(40010, "method.argument.not.valid"),
    INVALID_CODE_FORMAT(40011, "invalid.code.format"),
    INVALID_ADDITIONAL_ACTIVITY_VALUE(40012, "invalid.additional.activity.value"),
    INVALID_PREFERENCE_MEETING_TYPE_VALUE(40013, "invalid.preference.meeting.type.value"),
    TEAM_ALREADY_EXISTS(40014, "team.already.exist"),
    INVITATION_ALREADY_EXISTS(40015, "invitation.already.exist"),
    TEAM_ALREADY_ACTIVE(40016, "team.already.active"),
    GENDER_NOT_MATCH(40017, "gender.not.match"),
    INVITATION_ALREADY_SET(40018, "invitation.already.set"),
    NOT_BELONG_TO_TEAM(40019, "not.belong.to.team"),
    MANAGER_SELF_DELETION(40020, "manager.self.deletion"),
    NON_TEAM_MEMBER(40021, "non.team.member"),
    MEMBER_HAS_BEEN_DELETED(40022, "member.has.been.deleted"),
    TEAM_HAS_BEEN_DELETED(40023, "team.has.been.deleted"),
    INVALID_HTTP_REQUEST(40024, "invalid.http.request"),
    INVALID_DATABASE_KEY_TO_ENUM(40025, "invalid.database.key.to.enum"),
    INVALID_NICKNAME_FORMAT(40026, "invalid.nickname.format"),
    VALIDATION_ERROR(40027, "validation.error"),
    HTTP_MESSAGE_NOT_READABLE(40028, "http.message.not.readable"),


    NOTFOUND_SMS_CREDENTIAL(40100, "notfound.sms.credential"),
    MEMBER_NOT_FOUND(40101, "member.not.found"),
    MEMBER_NOT_FOUND_BY_ID(40102, "member.not.found.by.id"),
    PROFILE_IMAGE_NOT_FOUND(40103, "profile.image.not.found"),
    CODE_NOT_FOUND(40104, "code.not.found"),
    INVITED_MEMBER_NOT_FOUND(40105, "invited.member.not.found"),
    INVITATION_NOT_FOUND(40106, "invitation.not.found"),

    ACCESS_TOKEN_EXPIRED(40200, "access.token.expired"),
    REFRESH_TOKEN_EXPIRED(40201, "refresh.token.expired"),
    JWT_SIGNATURE_MISMATCH(40202, "jwt.signature.mismatch"),
    JWT_CLAIM_INCORRECT(40203, "jwt.claim.incorrect"),
    JWT_DECODE(40204, "jwt.decode"),
    REFRESH_TOKEN_MISMATCH(40205, "refresh.token.mismatch"),
    REFRESH_TOKEN_NOT_FOUND(40206, "refresh.token.not.found"),
    ACCESS_TOKEN_NOT_FOUND(40207, "access.token.not.found"),

    UNAUTHORIZED(40300, "unauthorized"),
    UNAUTHORIZED_ROLE(40301, "unauthorized.role"),
    UNAUTHORIZED_ROLE_MANAGER(40302, "unauthorized.role.manager"),
    UNAUTHORIZED_ROLE_ADMIN(40303, "unauthorized.role.admin"),
    UNAUTHORIZED_CREDIT(40304, "unauthorized.credit"),
    UNAUTHORIZED_MEMBER_PROFILE(40305, "unauthorized.member.profile"),
    UNAUTHORIZED_PROFILE_IMAGE(40306, "unauthorized.profile.image"),
    UNAUTHORIZED_UNIV(40307, "unauthorized.univ"),

    DATA_ACCESS(40400, "data.access"),

    UNEXPECTED_INTERNAL(50000, "unexpected.internal"),
    MISSING_REQUEST_PARAMETER(50001, "missing.request.parameter"),
    DATA_ENCRYPTION_ERROR(50002, "data.encryption.error"),
    MAIL_JSON_PARSING_ERROR(50003, "mail.json.parsing.error"),
    DATA_DECRYPTION_ERROR(50004, "data.decryption.error"),

    AWS_SNS_MESSAGE_TRANSFER_ERROR(50100, "aws.sns.message.transfer.error"),

    AWS_SES_EMAIL_TRANSFER_ERROR(50200, "aws.ses.email.transfer.error"),

    AWS_S3_OBJECT_UPLOAD_ERROR(50300, "aws.s3.object.upload.error"),
    AWS_S3_OBJECT_DELETE_ERROR(50301, "aws.s3.object.delete.error"),
    AWS_S3_FILE_CONVERSION_ERROR(50302, "aws.s3.file.conversion.error");


    private final int code;
    private final String messageKey;

    ErrorCode(int code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }
}
