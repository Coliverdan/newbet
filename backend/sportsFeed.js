import { supabase } from './supabaseClient.js';

const FOOTBALL_API_KEY = process.env.FOOTBALL_API_KEY || 'newbet_live_sports_feed';

/**
 * Top European & African Competitions
 */
const LEAGUES = [
  { id: 'ucl', name: 'UEFA Champions League', flag: '🇪🇺' },
  { id: 'epl', name: 'Premier League', flag: '🏴󠁧󠁢󠁥󠁮󠁧󠁿' },
  { id: 'laliga', name: 'La Liga', flag: '🇪🇸' },
  { id: 'can', name: 'CAN TotalEnergies', flag: '🌍' },
  { id: 'seriea', name: 'Serie A', flag: '🇮🇹' }
];

/**
 * High-profile upcoming and live matches pool with realistic sportsbook odds
 */
const UPCOMING_FIXTURES = [
  {
    id: "fix_ucl_1",
    sport: "Football",
    league: "UEFA Champions League",
    home_team: "Real Madrid",
    away_team: "Manchester City",
    home_flag: "🇪🇸",
    away_flag: "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
    match_time: "Demain 20:00",
    is_live: false,
    score: null,
    odds_1: 2.45,
    odds_x: 3.50,
    odds_2: 2.80,
    markets: [
      { name: "Les Deux Équipes Marquent", selections: [{ label: "Oui", odds: 1.62 }, { label: "Non", odds: 2.15 }] },
      { name: "Total Buts (Plus/Moins 2.5)", selections: [{ label: "+2.5 Buts", odds: 1.75 }, { label: "-2.5 Buts", odds: 2.05 }] },
      { name: "Double Chance", selections: [{ label: "1X (Real ou Nul)", odds: 1.42 }, { label: "12 (Real ou Man City)", odds: 1.28 }, { label: "X2 (Nul ou Man City)", odds: 1.55 }] }
    ]
  },
  {
    id: "fix_ucl_2",
    sport: "Football",
    league: "UEFA Champions League",
    home_team: "Arsenal FC",
    away_team: "Bayern Munich",
    home_flag: "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
    away_flag: "🇩🇪",
    match_time: "72 min",
    is_live: true,
    score: "2 - 1",
    odds_1: 1.55,
    odds_x: 4.10,
    odds_2: 5.20,
    markets: [
      { name: "Prochain But (But 4)", selections: [{ label: "Arsenal", odds: 2.10 }, { label: "Pas de but", odds: 2.40 }, { label: "Bayern", odds: 3.20 }] },
      { name: "Total Buts (Plus/Moins 3.5)", selections: [{ label: "+3.5 Buts", odds: 1.85 }, { label: "-3.5 Buts", odds: 1.90 }] }
    ]
  },
  {
    id: "fix_laliga_1",
    sport: "Football",
    league: "La Liga",
    home_team: "FC Barcelona",
    away_team: "Atlético Madrid",
    home_flag: "🇪🇸",
    away_flag: "🇪🇸",
    match_time: "Ce soir 21:00",
    is_live: false,
    score: null,
    odds_1: 1.95,
    odds_x: 3.40,
    odds_2: 3.90,
    markets: [
      { name: "Total Buts (Plus/Moins 2.5)", selections: [{ label: "+2.5 Buts", odds: 1.80 }, { label: "-2.5 Buts", odds: 1.95 }] },
      { name: "Les Deux Équipes Marquent", selections: [{ label: "Oui", odds: 1.70 }, { label: "Non", odds: 2.05 }] }
    ]
  },
  {
    id: "fix_can_1",
    sport: "Football",
    league: "CAN TotalEnergies",
    home_team: "Côte d'Ivoire",
    away_team: "Sénégal",
    home_flag: "🇨🇮",
    away_flag: "🇸🇳",
    match_time: "38 min",
    is_live: true,
    score: "1 - 0",
    odds_1: 1.90,
    odds_x: 3.10,
    odds_2: 3.80,
    markets: [
      { name: "Deuxième But", selections: [{ label: "Côte d'Ivoire", odds: 2.50 }, { label: "Sénégal", odds: 2.80 }, { label: "Aucun autre but", odds: 2.20 }] },
      { name: "Score Exact", selections: [{ label: "1 - 0", odds: 2.40 }, { label: "2 - 0", odds: 4.50 }, { label: "1 - 1", odds: 3.60 }] }
    ]
  },
  {
    id: "fix_epl_1",
    sport: "Football",
    league: "Premier League",
    home_team: "Liverpool",
    away_team: "Chelsea",
    home_flag: "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
    away_flag: "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
    match_time: "Dimanche 16:30",
    is_live: false,
    score: null,
    odds_1: 1.80,
    odds_x: 3.90,
    odds_2: 4.20,
    markets: [
      { name: "Les Deux Équipes Marquent", selections: [{ label: "Oui", odds: 1.55 }, { label: "Non", odds: 2.30 }] },
      { name: "Total Buts (Plus/Moins 2.5)", selections: [{ label: "+2.5 Buts", odds: 1.65 }, { label: "-2.5 Buts", odds: 2.20 }] }
    ]
  },
  {
    id: "fix_epl_2",
    sport: "Football",
    league: "Premier League",
    home_team: "Aston Villa",
    away_team: "Tottenham Hotspur",
    home_flag: "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
    away_flag: "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
    match_time: "Dimanche 14:00",
    is_live: false,
    score: null,
    odds_1: 2.30,
    odds_x: 3.60,
    odds_2: 2.95,
    markets: [
      { name: "Les Deux Équipes Marquent", selections: [{ label: "Oui", odds: 1.48 }, { label: "Non", odds: 2.50 }] }
    ]
  },
  {
    id: "fix_seriea_1",
    sport: "Football",
    league: "Serie A",
    home_team: "Inter Milan",
    away_team: "Juventus",
    home_flag: "🇮🇹",
    away_flag: "🇮🇹",
    match_time: "Dimanche 20:45",
    is_live: false,
    score: null,
    odds_1: 1.85,
    odds_x: 3.40,
    odds_2: 4.40,
    markets: [
      { name: "Double Chance", selections: [{ label: "1X (Inter ou Nul)", odds: 1.20 }, { label: "X2 (Nul ou Juve)", odds: 1.90 }] }
    ]
  }
];

/**
 * Synchronize fixtures into Supabase
 */
export async function syncFixturesToSupabase() {
  console.log(`[SportsFeed] Syncing ${UPCOMING_FIXTURES.length} fixtures to Supabase...`);
  
  for (const f of UPCOMING_FIXTURES) {
    await supabase.from('fixtures').upsert({
      id: f.id,
      sport: f.sport,
      league: f.league,
      home_team: f.home_team,
      away_team: f.away_team,
      home_flag: f.home_flag,
      away_flag: f.away_flag,
      match_time: f.match_time,
      is_live: f.is_live,
      score: f.score,
      odds_1: f.odds_1,
      odds_x: f.odds_x,
      odds_2: f.odds_2,
      markets: f.markets,
      updated_at: new Date().toISOString()
    });
  }

  return {
    success: true,
    syncedCount: UPCOMING_FIXTURES.length,
    timestamp: new Date().toISOString()
  };
}
