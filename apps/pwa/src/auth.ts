import { computed, reactive } from 'vue';
import type { AppRole, SessionContext } from '@baby-breakfast/shared';
import { getThemeByRole } from '@baby-breakfast/shared';
import { getSession, login, switchRole } from './api';

type AuthState = {
  session: SessionContext | null;
  loading: boolean;
  ready: boolean;
  error: string;
};

const TOKEN_KEY = 'baby-breakfast-token';

const state = reactive<AuthState>({
  session: null,
  loading: false,
  ready: false,
  error: '',
});

function persistToken(token: string | null) {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token);
    return;
  }

  localStorage.removeItem(TOKEN_KEY);
}

function applySession(session: SessionContext | null) {
  state.session = session;
  persistToken(session?.token ?? null);
}

export async function bootstrapAuth() {
  const token = localStorage.getItem(TOKEN_KEY);
  if (!token) {
    state.ready = true;
    return;
  }

  state.loading = true;
  state.error = '';
  try {
    applySession(await getSession());
  } catch {
    applySession(null);
  } finally {
    state.loading = false;
    state.ready = true;
  }
}

export async function loginWithPassword(username: string, password: string) {
  state.loading = true;
  state.error = '';
  try {
    applySession(await login(username, password));
  } catch (error) {
    state.error = error instanceof Error ? error.message : '登录失败';
    throw error;
  } finally {
    state.loading = false;
    state.ready = true;
  }
}

export async function switchCurrentRole(memberId: number, role: AppRole) {
  state.loading = true;
  state.error = '';
  try {
    applySession(await switchRole(memberId, role));
  } finally {
    state.loading = false;
  }
}

export function logout() {
  applySession(null);
  state.ready = true;
}

export const authState = {
  state,
  isAuthenticated: computed(() => Boolean(state.session)),
  role: computed<AppRole>(() => state.session?.currentMember.role ?? 'parent'),
  theme: computed(() => getThemeByRole(state.session?.currentMember.role ?? 'parent')),
};
