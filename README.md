# OasisQuests Documentation

## Commands

### Quests
Format:
  /quests OR /q

Description:
  This command opens the GUI for the plugin.

### Campaign
Format:
  /q campaign

Description:
  This command opens the GUI for campaign quests.

### Daily
Format:
  /q daily
  /q daily {activate/deactivate} {quest-name}

Description:
  This command opens the GUI for daily quests, and allows the user to activate and deactivate a given quest.

### Mission
Format:
  /q mission
  /q mission {activate/deactivate} {quest-name}

Description:
  This command opens the GUI for mission quests, and allows the user to activate and deactivate a given mission.

### Special
Format:
  /q special
  /q special {activate/deactivate} {quest-name}

Description:
  This command opens the GUI for special quests, and allows the user to activate and deactivate a given special quest.
  
### Item Rewards
Format:
  /q itemrewards

Description:
  This command opens the GUI for managing item rewards.

### Kit
Format:
  /q kit {boss}

Description:
  This command opens the GUI for setting the kit for the given boss.

### Reroll
Format:
  /q reroll

Description:
  Rerolls the Daily Quests.

### Set Tier
Format:
  /q settier {player} {mission / all} {tier-number}

Description:
  Sets the tier of all missions or a given mission for a selected player.

### Voucher
Format:
  /q voucher give {player} {special-quest} {amount}

Description:
  Gives the player a voucher (or multiple) to allow them to do a given special quest.
  
### Reload
Format:
  /q reload

Description:
  Reloads the plugin.

  
## Permissions

### Default
Format:
  quests.default

Description:
  This is the basic permission that allows players to access commands in OasisQuests.

### Admin
Format:
  quests.admin

Description:
  This is the permission that allows users of power to set tiers and use other commands for managing the plugin.

### Active Quest Limits
Format:
  quests.{quest-type}.limit.{amount}

Description:
  This sets the limit for the amount of quests of a certain quest type that a user can have active at a time.

Valid Amounts:
  Any Integer value greater than 0.

Additional Information:
  Does not include campaign quests as they are automatically activated and disactivated as the players works their way through quests.

### Specials
Format:
  quests.special.{quest-name}

Description:
  Allows the user to do a specified special quest.


## Placeholders
Format:
  quests_active_{quest-type}_{number}

Description:
  Returns the first requirement of the specified quests type. The number specifies which selected quest to show, so 1 would be their first active quest, 2 the second, and so on.


## Additional Information
Quest Types:
  Mission, Daily, Special, Campaign

  




