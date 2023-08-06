package com.e2i.wemeet.domain.code;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeRepository extends JpaRepository<Code, CodePk> {

    Optional<Code> findByCodeValue(String codeValue);

}
