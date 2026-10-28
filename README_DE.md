# NT-overenchant

[![Sprache: Deutsch](https://img.shields.io/badge/Sprache-Deutsch-orange.svg)](#)
[![English](https://img.shields.io/badge/Language-English-blue.svg)](README.md)
[![Plattform: Paper / Purpur / Spigot](https://img.shields.io/badge/Plattform-Paper%20%7C%20Purpur%20%7C%20Spigot-green.svg)](#)
[![Minecraft: 1.8.8 - 1.21.4+](https://img.shields.io/badge/Minecraft-1.8.8--1.21.4%2B-brightgreen.svg)](#)
[![Java: 8+](https://img.shields.io/badge/Java-8%2B-red.svg)](#)
[![Donate with PayPal](https://img.shields.io/badge/Donate-PayPal-00457C.svg?logo=paypal&logoColor=white)](https://paypal.me/niktrick)
[![Support on Ko-fi](https://img.shields.io/badge/Support-Ko--fi-FF5E5B.svg?logo=kofi&logoColor=white)](https://ko-fi.com/niktrick)

[🇬🇧 Switch to English Version](README.md) | [🇩🇪 Deutsche Version](README_DE.md)

---

Universelles, leichtgewichtiges Verzauberungs-Erweiterungs-Plugin für Minecraft **1.8.8 bis 1.21.4+** (Paper, Purpur, Spigot).

Ermöglicht es Spielern, im Amboss gleiche Verzauberungsstufen über das reguläre Vanilla-Maximum hinaus zu kombinieren – basierend auf der vertrauten Vanilla-Amboss-Mechanik (z. B. Schärfe V + V = VI).

---

## Funktionsweise

Vanilla Minecraft steigert Verzauberungsstufen automatisch um 1, wenn zwei gleiche Stufen im Amboss kombiniert werden (z. B. Schutz IV + Schutz IV = Schutz V). Allerdings stoppt Vanilla strikt am vorgegebenen Maximallevel.

**OverEnchant hebt dieses Limit nahtlos auf**:
```
Effizienz V + V     -> Effizienz VI
Effizienz VI + VI   -> Effizienz VII
...
Effizienz XIV + XIV -> Effizienz XV (Konfigurierbarer Max-Effekt: Sofortabbau aller Blöcke!)
```

### Kernprinzipien
- **Faires Survival-Gameplay**: Höhere Stufen entstehen nur durch legitimes Zusammenfügen zweier Gegenstände/Bücher gleicher Stufe.
- **Konfigurierbare Höchststufen**: Jede Verzauberung besitzt eine eigene Obergrenze (`cap`) in der `config.yml`.
- **Balancierte XP-Kosten**: Ein konfigurierbarer XP-Aufschlag (`anvil.xp-cost-per-overlevel`) wird für jede Stufe über dem Vanilla-Limit berechnet.
- **Spezialeffekte**: Optionale Effekte wie `instamine` bei maximaler Effizienz.

---

## Befehle & Permissions

| Befehl | Beschreibung | Permission |
| :--- | :--- | :--- |
| `/overenchant info` | Zeigt Plugin-Informationen und Funktionsweise an | *Keine* |
| `/overenchant reload` | Lädt die `config.yml` neu | `overenchant.admin` |
| `/overenchant lang <de\|en>` | Ändert die Sprache des Plugins live (Deutsch / Englisch) | `overenchant.admin` |

---

## Konfiguration (`config.yml`)

```yaml
# Spracheinstellung: "en" für Englisch, "de" für Deutsch
# Kann auch im Spiel geändert werden mit /overenchant lang <de|en>
language: "en"

anvil:
  # Zusätzliche XP-Level pro Stufe ÜBER dem normalen Vanilla-Maximum
  xp-cost-per-overlevel: 3

enchantments:
  # Werkzeuge & Waffen
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
  # ... unterstützt alle Vanilla- & modernen Verzauberungen bis 1.21.4+ (Density, Breach, Wind Burst)
```

> **Survival-Hinweis**: Wenn beim Kombinieren im Amboss "Zu teuer!" angezeigt wird, erhöhe `settings.anvil-max-repair-cost` in der `spigot.yml` deines Servers (oder setze den Wert auf `-1`, um Reparaturkostenlimits aufzuheben).

---

## Kompatibilität

- **Minecraft**: 1.8.8, 1.12.2, 1.16.5, 1.17, 1.18, 1.19, 1.20, 1.21.x+
- **Server-Software**: Paper, Purpur, Spigot
- **Java**: 8 bis 25+ (kompiliert auf Java 8 Bytecode für maximale Kompatibilität)

---

## 💖 Unterstützung & Spenden

Wenn dir dieses Plugin gefällt, freuen wir uns über deine freiwillige Unterstützung:
- [PayPal Spende](https://paypal.me/niktrick)
- [Ko-fi Support](https://ko-fi.com/niktrick)
- Alle Spenden sind freiwillig und fließen direkt in die Wartung und Weiterentwicklung!
