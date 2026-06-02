import type { FamilyMember } from '@baby-breakfast/shared';
import { request } from '../api';

export type CreateMemberRequest = {
  name: string;
  role: 'parent' | 'child' | 'other';
  gender: 'male' | 'female' | 'other';
  birthday: string | null;
  avatarUrl?: string | null;
  allergens?: string[];
  dietaryRestrictions?: string[];
  preferences?: string[];
};

export type UpdateMemberRequest = Partial<CreateMemberRequest>;

export function getMembers(): Promise<FamilyMember[]> {
  return request('/api/members');
}

export function getMember(id: number): Promise<FamilyMember> {
  return request(`/api/members/${id}`);
}

export function createMember(data: CreateMemberRequest): Promise<FamilyMember> {
  return request('/api/members', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function updateMember(id: number, data: UpdateMemberRequest): Promise<FamilyMember> {
  return request(`/api/members/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

export function deleteMember(id: number): Promise<void> {
  return request(`/api/members/${id}`, {
    method: 'DELETE',
  });
}

export async function uploadImage(file: File): Promise<string> {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch('/api/upload/image', {
    method: 'POST',
    body: formData,
  });
  
  if (!response.ok) {
    throw new Error(`Upload failed with status ${response.status}`);
  }
  
  const result = await response.json();
  return result.data;
}
