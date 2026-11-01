import { createClient } from '@supabase/supabase-js';

const SUPABASE_URL = process.env.SUPABASE_URL || 'https://bovozexwmruwjywzhhao.supabase.co';
const SUPABASE_SERVICE_ROLE_KEY = process.env.SUPABASE_SERVICE_ROLE_KEY || 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJvdm96ZXh3bXJ1d2p5d3poaGFvIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc5MzUzMzM5MSwiZXhwIjoyMTA5MTA5MzkxfQ.5bVkl5lIkTdKULREB01PLDMUNdM8NaqHVKmnunnL9qQ';

export const supabase = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY, {
  auth: { persistSession: false }
});

