# Valentines

**Original Valentines plugin. Everything in one.**  
*Let your players be together!*

---

## Features
- GUI: Keep it simple with only `/vgui` command
- Marriages: Marry your favorite person
- Achievements: Achievements you unlock while playing
- Mood: 5 types of mood
- Effects: Look better with effects around you
- Kiss, Hug, Like: Default valentines options

---

## Commands & Permissions

| Command       | Alias | Description                 | Permission           |
|---------------|-------|---------------------|--------------------|
| `/valentines`    | `/val`, `/love` | Displays all available commands in chat | `valentines.use`   |
| `/valentines reload`   | - | Reloads configuration | `valentines.reload`  |
| `/vgui`              | `/vg`, `/lovegui` | Opens main GUI | `valentines.use`  |

---

## Images / Screenshots

Section "**Gallery**"

---

## Files
<details>
  <summary><strong>config.yml</strong></summary>

```yaml
# ╔════════════════════════════════════════════════════════════════════╗
# ║                     VALENTINES PLUGIN CONFIG                       ║
# ║                     Update: February 20, 2026                      ║
# ╚════════════════════════════════════════════════════════════════════╝

# Internal version tracking - DO NOT CHANGE THIS VALUE
config-version: "2026+1.3"

# ╔════════════════════════════════════════════════════════════════════╗
# ║                          GENERAL SETTINGS                          ║
# ╚════════════════════════════════════════════════════════════════════╝

# Displayed before all plugin messages in chat
Prefix: "&d&lValentines &7»&r "

# ╔════════════════════════════════════════════════════════════════════╗
# ║                       CHAT SYMBOL REPLACEMENT                      ║
# ╚════════════════════════════════════════════════════════════════════╝

# Enable automatic replacement of specified words with a custom symbol in chat
# When true, words listed below will be replaced with the symbol when typed
symbol-change: true

# Changes color of symbol
symbol-color: "&c"

# The actual symbol that will replace the words
# Unicode symbols are supported
symbol: "♥"

# List of words that will be replaced with the symbol in player chat
words:
  - "love"
  - "valentines"
  - "heart"

# ╔════════════════════════════════════════════════════════════════════╗
# ║                      PARTICLE EFFECT SETTINGS                      ║
# ╚════════════════════════════════════════════════════════════════════╝

# Global particle effect configuration for players
effect:
  # Enables/disables effects
  enabled: true

  # Number of particles spawned per effect tick (higher = more particles, more lag)
  particle-density: 2

  # How often effects update in ticks (20 ticks = 1 second)
  update-rate: 3

  # Maximum height particles can reach above player (in blocks)
  max-height: 2.5

  # Starting height of particles above player's feet (in blocks)
  start-height: 0.1

  # Horizontal radius of particle effects (in blocks)
  radius: 1.0

# Enable/disable specific action particle effects
# These show when players use commands like /kiss, /hug, or get married
kiss-effect: true
marriage-effect: true
hug-effect: true

# ╔════════════════════════════════════════════════════════════════════╗
# ║                           WORLD SETTINGS                           ║
# ╚════════════════════════════════════════════════════════════════════╝

# List of worlds where the plugin features are active
enabled-worlds:
  - "world"
  - "world_nether"
  - "world_the_end"

# ╔════════════════════════════════════════════════════════════════════╗
# ║                          COOLDOWN SETTINGS                         ║
# ╚════════════════════════════════════════════════════════════════════╝

# Cooldowns prevent spam and add meaningful weight to actions
cooldowns:
  # How long players must wait between hugging others
  # Value is in SECONDS
  hug: 45

  # How long players must wait between kissing others
  # Value is in SECONDS
  kiss: 60

  # How long players must wait between liking different players
  # Value is in SECONDS
  # Note: Experimental function
  like: 240

  # How long players must wait between changing their mood status
  # Value is in SECONDS
  mood-change: 120

# ╔════════════════════════════════════════════════════════════════════╗
# ║                      MARRIAGE SYSTEM SETTINGS                      ║
# ╚════════════════════════════════════════════════════════════════════╝

marriage:
  # How long a marriage proposal stays valid before expiring
  # Value is in SECONDS
  proposal-timeout: 45

  # Cooldown between sending marriage proposals
  # Value is in SECONDS
  proposal-cooldown: 30

  # Whether players must type /marry confirm to accept proposals
  # If false, proposals are instantly accepted
  require-confirmation: true

  # Broadcast marriage announcements to the entire server
  # Shows "{Player1} and {Player2} just got married!"
  announcement: true

  # Require players to type /divorce confirm before divorcing
  # Adds safety against accidental divorces
  divorce-confirmation: true

  # Cooldown after divorce before player can marry again
  # Prevents instant remarrying after divorce
  # Value is in SECONDS
  divorce-cooldown: 600

# ╔════════════════════════════════════════════════════════════════════╗
# ║              ANNIVERSARY SYSTEM SETTINGS (EXPERIMENTAL)            ║
# ╚════════════════════════════════════════════════════════════════════╝

anniversary:
  # Checks marriages and announces anniversaries when dates match
  enabled: true

  # Who sees anniversary announcements
  # Options: "global" (everyone), "couple" (only the married pair), "world" (specific worlds)
  announcement-type: "global"

  # If announcement-type is "world", specify which worlds see announcements
  announcement-worlds:
    - "world"

  # How often to check for anniversaries
  # Value is in SECONDS
  check-interval: 300

# ╔════════════════════════════════════════════════════════════════════╗
# ║                         MOOD SYSTEM SETTINGS                       ║
# ╚════════════════════════════════════════════════════════════════════╝

mood:
  # Enable the mood system allowing players to set their emotional state
  enabled: true

  # Require players to be married before they can set/share moods
  # If true, only married players can use /mood command
  require-marriage: false

  # Notify partner when player changes their mood
  # Partner receives message like "Your partner is now feeling: Happy"
  partner-notifications: true

  # Available mood types players can select
  # These appear in the mood GUI and /mood command
  mood-types:
    - "very-good"
    - "good"
    - "neutral"
    - "bad"
    - "very-bad"

# ╔════════════════════════════════════════════════════════════════════╗
# ║                        LEADERBOARD SETTINGS                        ║
# ╚════════════════════════════════════════════════════════════════════╝

leaderboard:
  # Enable the love leaderboard showing top players by hugs/kisses/likes
  enabled: true

# ╔════════════════════════════════════════════════════════════════════╗
# ║                PERFORMANCE MONITORING (EXPERIMENTAL)               ║
# ╚════════════════════════════════════════════════════════════════════╝

performance:
  # Enable performance monitoring and memory usage tracking
  # Monitors plugin resource usage and warns about high memory consumption
  monitoring-enabled: true

  # How often to check performance metrics
  # Value is in SECONDS
  monitor-interval: 300

  # Log detailed performance metrics to console
  # Shows commands executed, effects rendered, GUI opens, etc.
  detailed-logging: false

  # Memory usage threshold (%) that triggers warnings
  # When memory usage exceeds this, plugin suggests garbage collection
  memory-threshold: 85

# ╔════════════════════════════════════════════════════════════════════╗
# ║                          DATA MANAGEMENT                           ║
# ╚════════════════════════════════════════════════════════════════════╝

# How often to automatically save all player data to disk
# Lower = more frequent saves
# Value is in MINUTES
auto-save: 30

# ╔════════════════════════════════════════════════════════════════════╗
# ║                         ACHIEVEMENT SYSTEM                         ║
# ╚════════════════════════════════════════════════════════════════════╝
achievements:
  # Enable the achievement system for tracking player milestones
  # Includes first kiss, first hug, marriage, reaching 10 hugs/kisses, etc.
  enabled: true

  # Broadcast achievement unlocks to all players on the server
  broadcast: true

  # Play a sound effect when player unlocks an achievement
  # Uses Minecraft's UI_TOAST_CHALLENGE_COMPLETE sound
  sound-enabled: true
```
</details>

<details>
  <summary><strong>lang/en.yml</strong></summary>

```yaml
plugin:
  reload: "&aValentines plugin has been reloaded successfully!"

general:
  no-permission: "&cYou don't have permission to use this command!"
  player-offline: "&cThat player is not online right now!"
  player-not-found: "&cPlayer not found!"
  partner-offline: "&cYour partner is not online!"
  cooldown: "&cYou can {command} again in &d{time}&c seconds!"
  invalid-world: "&cThis command cannot be used in this world!"
  feature-disabled: "&cThis feature is currently disabled!"

help:
  header: "&d❤ &5VALENTINES COMMANDS &d❤"
  gui: "&d/vgui &f- Opens GUI"

hug:
  usage: "&cUsage: &d/hug <player>"
  self: "&cYou can't hug yourself!"
  sent: "&fYou gave &d{player} &fa hug!"
  received: "&d{player} &fgave you a hug!"
  broadcast: "&d{sender} &fand &d{receiver} &fhugged!"

kiss:
  usage: "&cUsage: &d/kiss <player>"
  self: "&cYou can't kiss yourself!"
  sent: "&fYou kissed &d{player}&f!"
  received: "&d{player} &fkissed you!"
  broadcast: "&d{sender} &fand &d{receiver} &fkissed!"

like:
  usage: "&cUsage: &d/like <player>"
  self: "&cYou already like yourself!"
  already-liked: "&cYou've already liked &d{player}&c!"
  cooldown: "&cYou can like again in &d{time}&c seconds!"
  sent: "&fYou liked &d{player}&f!"
  received: "&d{player} &flikes you!"

marry:
  usage: "&cUsage: &d/marry <player> &for &d/marry confirm/deny"
  self: "&cYou can't marry yourself!"
  proposal-sent: "&fMarriage proposal sent to &d{player}&f! Good luck!"
  proposal-received: "&d{player} &fwants to marry you! \n&fType &d/marry confirm &fto accept or &d/marry deny &fto decline."
  already-married: "&cYou're already married to &d{partner}&c!"
  target-already-married: "&c{player} is already taken!"
  no-pending-proposals: "&cYou have no pending marriage proposals!"
  proposer-offline: "&cThe person who proposed is no longer online!"
  proposal-cooldown: "&cYou can propose again in &d{time}&c seconds!"
  accept-sender: "&a{player} &aaccepted your proposal! You're now married!"
  accept-receiver: "&aYou married &d{player}&a!"
  decline-sender: "&c{player} declined your proposal."
  decline-receiver: "&cYou declined &d{player}'s &cproposal."
  divorce-initiator: "&cYou have divorced &d{player}&c."
  divorce-target: "&c{player} &chas divorced you."
  divorce-confirm: "&eAre you sure you want to divorce &d{player}&e? Type &d/divorce confirm &eto proceed."
  divorce-timeout: "&cDivorce confirmation expired. Use &d/divorce &cagain."
  divorce-cooldown: "&cYou must wait &d{time}&c seconds before you can divorce again!"
  not-married: "&cYou're not married!"
  not-married-to: "&cYou're not married to &d{player}&c!"
  announcement: "&f{player1} &fand &d{player2} &fjust got married! &d Congratulations!"
  divorce-announcement: "&c{player1} &fand &c{player2} &fhave divorced."

anniversary:
  global-announcement: "&d&5ANNIVERSARY CELEBRATION! &f{player1} &fand &d{player2} &fcelebrate &d{years} &fyear(s) of marriage!"
  couple-message: "&d&5Happy Anniversary! &fYou and &d{partner} &fhave been married for &d{years} &fyear(s)!"
  world-announcement: "&d&f{player1} &fand &d{player2} &fcelebrate their &d{years} &fyear anniversary!"

mood:
  not-married: "&cYou must be married to share your mood!"
  updated: "&fYour mood has been updated to: &d{mood}"
  partner-notified: "&fYour partner has been notified of your mood change!"
  partner-mood-changed: "&fYour partner &d{partner} &fis now feeling: &d{mood}"
  unknown: "&7Unknown"
  very-good: "&aVery Good"
  good: "&eGood"
  neutral: "&fNeutral"
  bad: "&cBad"
  very-bad: "&4Very Bad"

stats:
  usage: "&cUsage: &d/valentinesplayer <player>"
  header: "&5{player}'s Valentine Stats"
  kisses: "&dKisses received: &f{count}"
  hugs: "&dHugs received: &f{count}"
  likes: "&dLikes received: &f{count}"
  married: "&dMarried to: &f{partner} &d(for {days} days)"
  single: "&dRelationship status: &fSingle"
  total-score: "&dTotal Score: &f{score}"
  mood: "&dCurrent mood: &f{mood}"

leaderboard:
  header: "&5Valentine Leaderboard"
  entry: "&a#{rank} &d{player} &f- &d{score} &fpoints &7(&d{kisses}&f, &d{hugs}&f, &d{likes}&7)"
  empty: "&cNo players found on the leaderboard yet!"

marriages:
  header: "&5Married Couples "
  couple: "&f&d{player1} &f& &d{player2} &f- &d{days} &fdays together"
  none: "&dThere are no married couples on the server yet!"

player-search:
  prompt: "&fPlease type the name of the player you want to search for:"
  not-found: "&cPlayer '{player}' not found or is not online!"
  searching: "&fSearching for player: &d{player}&f..."

gui:
  main-menu-title: "&d❤ Valentines Menu ❤"
  stats-menu-title: "&d❤ &5Player Stats &d❤"
  leaderboard-title: "&d❤ &5Love Leaderboard &d❤"
  marriages-title: "&d❤ &5Married Couples &d❤"
  settings-title: "&d❤ &5Effect Settings &d❤"
  mood-title: "&d❤ &5Mood Settings &d❤"
  achievements-menu-title: "&d❤ &5Achievements &d❤"
  player-profile-title: "&d❤ &5{player}'s Profile &d❤"

  stats-button-title: "&dYour Stats"
  stats-button-lore:
    - "&fView your statistics"
    - "&7See what and from who you received"
  leaderboard-button-title: "&dLeaderboard"
  leaderboard-button-lore:
    - "&fSee the most loved players"
    - "&7Compete for the top spot!"
  marriages-button-title: "&dMarried Couples"
  marriages-button-lore:
    - "&fView all marriages on the server"
    - "&7See who is really married!"
  settings-button-title: "&dSettings"
  settings-button-lore:
    - "&fCustomize your particle effects"
    - "&7Just don't forget to allow animations!"
  mood-button-title: "&dMood"
  mood-button-lore:
    - "&fSet your current mood"
    - "&7Let others know how you feel!"
  achievements-button-title: "&dAchievements"
  achievements-button-lore:
    - "&fView your achievements"
    - "&7Track your progress!"
  player-search-button-title: "&dPlayer Search"
  player-search-button-lore:
    - "&fSearch for other players"
    - "&7Find and interact with players!"

  hugs-title: "&dHugs Received"
  hugs-lore:
    - "&fYou've received &d{count} &f hugs"
    - "&7Do you actually like it?"
  kisses-title: "&dKisses Received"
  kisses-lore:
    - "&fYou've received &d{count} &fsweet kisses"
    - "&7Maybe you should do your first move?"
  likes-title: "&dLikes Received"
  likes-lore:
    - "&fYou've received &d{count} &flikes"
    - "&7People really appreciate you!"
  marriage-title: "&dMarriage Status"
  marriage-lore:
    - "&fYou are married to &d{partner}"
    - "&fFor &d{days} &fdays"
    - "&7True love never dies!"
  single-title: "&dRelationship Status"
  single-lore:
    - "&fYou are currently single"
    - "&7Don't worry, somebody is out there!"
  mood-display-title: "&dYour Mood"
  mood-display-lore:
    - "&fCurrent mood: &d{mood}"
    - "&7Let others know how you feel!"

  leaderboard-entry-title: "&a#{rank} &d{player}"
  leaderboard-entry-lore:
    - "&fTotal Love Score: &d{score}"
    - "&fHugs: &d{hugs}"
    - "&fKisses: &d{kisses}"
    - "&fLikes: &d{likes}"

  marriage-couple-title: "&d{player1} &f♥ &d{player2}"
  marriage-couple-lore:
    - "&fMarried since: &d{date}"
    - "&fDays together: &d{days}"
    - "&7A beautiful love story!"

  hug-button-title: "&dHug {player}"
  hug-button-lore:
    - "&fGive them a hug"
    - "&7Feels good?"
  kiss-button-title: "&dKiss {player}"
  kiss-button-lore:
    - "&fSend them a kiss"
    - "&7That's good move!"
  like-button-title: "&dLike {player}"
  like-button-lore:
    - "&fShow your appreciation"
    - "&7Let them know you care!"
  marry-button-title: "&dMarry {player}"
  marry-button-lore:
    - "&fPropose marriage"
    - "&7Just hope for the best!"

  mood-very-good-title: "&aVery Good"
  mood-very-good-lore:
    - "&fSet your mood to Very Good"
    - "&7You're feeling amazing!"
  mood-good-title: "&eGood"
  mood-good-lore:
    - "&fSet your mood to Good"
    - "&7You're doing well!"
  mood-neutral-title: "&fNeutral"
  mood-neutral-lore:
    - "&fSet your mood to Neutral"
    - "&7You're feeling okay!"
  mood-bad-title: "&cBad"
  mood-bad-lore:
    - "&fSet your mood to Bad"
    - "&7What happened?"
  mood-very-bad-title: "&4Very Bad"
  mood-very-bad-lore:
    - "&fSet your mood to Very Bad"
    - "&7Why?"

  partner-mood-title: "&d{partner}'s Mood"
  partner-mood-lore:
    - "&fCurrent mood: &d{mood}"

  effect-spiral: "&dSpiral Effect"
  effect-heart: "&dHeart Effect"
  effect-cloud: "&dCloud Effect"
  effect-selected: "&aSelected"
  effect-disabled: "&cDisabled"

  back-button: "&c⬅ Back"
  close-button: "&cClose Menu"

achievements:
  unlocked: "&a&Achievement Unlocked! &d{achievement}"
  description: "&f{description}"
  broadcast: "&d{player} &fhas unlocked the achievement: &d{achievement}&f!"
  progress: "&fYou have unlocked &d{unlocked}&f/&d{total} &fachievements &7({percentage}%)"

  first_kiss_received:
    name: "First Kiss Received"
    description: "Received your first kiss from another player"
  first_hug_received:
    name: "First Hug Received"
    description: "Received your first hug from another player"
  first_like_received:
    name: "First Like Received"
    description: "Received your first like from another player"
  first_kiss_given:
    name: "First Kiss Given"
    description: "Gave your first kiss to another player"
  first_hug_given:
    name: "First Hug Given"
    description: "Gave your first hug to another player"
  first_like_given:
    name: "First Like Given"
    description: "Gave your first like to another player"
  reach_10_kisses:
    name: "Kissable"
    description: "Received 10 kisses"
  reach_10_hugs:
    name: "Huggable"
    description: "Received 10 hugs"
  reach_10_likes:
    name: "Likeable"
    description: "Received 10 likes"
  marriage:
    name: "Just Married"
    description: "Got married to another player"
  divorce:
    name: "It's Complicated"
    description: "Got divorced from your partner"
  married_30_days:
    name: "One Month Together"
    description: "Been married for 30 days"
  married_365_days:
    name: "One Year Anniversary"
    description: "Been married for 365 days"
  married_2_years:
    name: "Two Years Strong"
    description: "Been married for 2 years"
  first_mood_set:
    name: "Mood Setter"
    description: "Set your mood for the first time"
```
</details>
