import type { AppRole, SessionContext } from '@baby-breakfast/shared';

type ApiEnvelope<T> = {
  code: number;
  message: string;
  data: T;
};

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, {
    headers: {
      'Content-Type': 'application/json',
      ...(init?.headers ?? {}),
    },
    ...init,
  });

  if (!response.ok) {
    throw new Error(`Request failed with status ${response.status}`);
  }

  const body = (await response.json()) as ApiEnvelope<T>;
  return body.data;
}

export { request };

export function login(username: string, password: string): Promise<SessionContext> {
  return request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });
}

export function getSession(): Promise<SessionContext> {
  return request('/api/auth/session');
}

export function switchRole(memberId: number, role: AppRole): Promise<SessionContext> {
  return request('/api/auth/switch-role', {
    method: 'POST',
    body: JSON.stringify({ memberId, role }),
  });
}
