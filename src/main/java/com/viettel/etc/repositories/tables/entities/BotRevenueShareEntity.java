package com.viettel.etc.repositories.tables.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.*;

/**
 * Autogen class Entity: Create Entity For Table Name Bot_revenue_share
 * 
 * @author ToolGen
 * @date Thu Jul 23 14:32:23 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "BOT_REVENUE_SHARE")
public class BotRevenueShareEntity implements Serializable{

    @Id
    @GeneratedValue(generator = "BOT_REVENUE_SHARE_SEQ")
    @SequenceGenerator(name = "BOT_REVENUE_SHARE_SEQ", sequenceName = "BOT_REVENUE_SHARE_SEQ", allocationSize = 1)
    @Column(name = "REVENUE_SHARE_ID")
    Long revenueShareId;

    @Column(name = "SERVICE_PLAN_ID")
    Long servicePlanId;

    @Column(name = "BOT_ID")
    Long botId;

    @Column(name = "BOT_NAME")
    String botName;

    @Column(name = "BOT_REVENUE")
    Double BotRevenue;
}