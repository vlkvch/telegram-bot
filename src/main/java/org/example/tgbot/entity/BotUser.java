package org.example.tgbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class BotUser {

    @Id
    @Column(name = "user_id", unique = true)
    private long id;

    @Accessors(fluent = true)
    @Column(name = "has_access")
    private boolean hasAccess;

    @Column(name = "is_subscribed")
    private boolean isSubscribed;

    @Column(name = "is_bot_creator")
    private boolean isBotCreator;

    @Column(name = "is_group_representative")
    private boolean isGroupRepresentative;

    @Column(name = "current_command")
    private String currentCommand;

    public void cancelCurrentCommand() {
        setCurrentCommand(null);
    }

}
