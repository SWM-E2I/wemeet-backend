package com.e2i.wemeet.domain.team;

import com.e2i.wemeet.domain.team.suggestion.SuggestionRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamCustomRepository,
    SuggestionRepository {

}
