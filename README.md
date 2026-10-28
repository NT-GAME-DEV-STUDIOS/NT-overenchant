# NT-overenchant

[![Language: English](https://img.shields.io/badge/Language-English-blue.svg)](#)
[![Deutsch](https://img.shields.io/badge/Sprache-Deutsch-orange.svg)](README_DE.md)
[![Platform: Paper / Purpur / Spigot](https://img.shields.io/badge/Platform-Paper%20%7C%20Purpur%20%7C%20Spigot-green.svg)](#)
[![Minecraft: 1.8.8 - 1.21.4+](https://img.shields.io/badge/Minecraft-1.8.8--1.21.4%2B-brightgreen.svg)](#)
[![Java: 8+](https://img.shields.io/badge/Java-8%2B-red.svg)](#)
[![Donate with PayPal](https://img.shields.io/badge/Donate-PayPal-00457C.svg?logo=paypal&logoColor=white)](https://paypal.me/niktrick)
[![Support on Ko-fi](https://img.shields.io/badge/Support-Ko--fi-FF5E5B.svg?logo=kofi&logoColor=white)](https://ko-fi.com/niktrick)

[🇩🇪 Zur deutschen Version wechseln](README_DE.md) | [🇬🇧 English Version](README.md)

---

Universal, lightweight enchantment expansion plugin for Minecraft **1.8.8 to 1.21.4+** (Paper, Purpur, Spigot).

Allows anvils to combine enchantments beyond their standard vanilla maximum levels using the familiar vanilla anvil mechanics (e.g. Sharpness V + V = VI).

---

## How It Works

Vanilla Minecraft naturally increases enchantment levels by one when combining two identical levels (e.g., Protection IV + IV = Protection V on an anvil). However, vanilla refuses to proceed past the enchantment's default cap.

**OverEnchant removes that ceiling cleanly**:
```
Efficiency V + V     -> Efficiency VI
Efficiency VI + VI   -> Efficiency VII
...
Efficiency XIV + XIV -> Efficiency XV (Configurable max-effect: Instant mine on any block!)
```

### Key Principles
- **No free shortcuts**: Higher tiers can only be obtained through legitimate anvil merging of two identical-level items/books.
- **Configurable Caps**: Every enchantment has its own configurable maximum cap in `config.yml`.
- **Balanced XP Cost**: A configurable XP penalty (`anvil.xp-cost-per-overlevel`) is applied for levels surpassing vanilla limits.
- **Special Effects**: Optional cap effects like `instamine` on Efficiency XV.

---

## Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/overenchant info` | Displays plugin information and available features | *None* |
| `/overenchant reload` | Reloads `config.yml` | `overenchant.admin` |
| `/overenchant lang <de\|en>` | Changes plugin language live (English / German) | `overenchant.admin` |

---

## Configuration (`config.yml`)

```yaml
# Language setting: "en" for English, "de" for German
# Can also be changed in-game with /overenchant lang <de|en>
language: "en"

anvil:
  # Extra XP levels charged per level gained ABOVE the enchantment's normal vanilla max level.
  xp-cost-per-overlevel: 3

enchantments:
  # Tools & Weapons
  efficiency:
    cap: 15
    max-effect: instamine
  sharpness:
    cap: 10
  unbreaking:
    cap: 10
  fortune:
    cap: 10
  looting:
    cap: 10
  # ... supports all vanilla & modern enchantments up to 1.21.4+ (Density, Breach, Wind Burst)
```

> **Survival Note**: If anvil merges show "Too Expensive!" for survival players, raise `settings.anvil-max-repair-cost` in your server's `spigot.yml` (or set it to `-1` to disable repair cost limits).

---

## Compatibility

- **Minecraft**: 1.8.8, 1.12.2, 1.16.5, 1.17, 1.18, 1.19, 1.20, 1.21.x+
- **Server Software**: Paper, Purpur, Spigot
- **Java**: 8 to 25+ (compiled to Java 8 bytecode for maximum compatibility)

---

## 💖 Support & Donations

If you enjoy this plugin, consider supporting future development:
- [PayPal Donation](https://paypal.me/niktrick)
- [Support on Ko-fi](https://ko-fi.com/niktrick)
- Donations are voluntary and directly help maintain multi-version compatibility!
