package org.example.tgbot.repository;

import java.util.Collection;
import java.util.Optional;

import org.example.tgbot.entity.BotUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotUserRepository extends CrudRepository<BotUser, Long> {

    @Query("select u from BotUser u where u.isBotCreator = true")
    Optional<BotUser> findBotCreator();

    @Query("select u from BotUser u where u.isGroupRepresentative = true")
    Optional<BotUser> findGroupRepresentative();

    @Query("select u from BotUser u where u.hasAccess = true")
    Collection<BotUser> findStudents();

}
