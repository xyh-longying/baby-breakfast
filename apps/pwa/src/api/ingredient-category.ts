import type { IngredientCategoryDTO, CreateIngredientCategoryRequest, UpdateIngredientCategoryRequest } from '@baby-breakfast/shared';
import { request } from '../api';

export function getIngredientCategories(): Promise<IngredientCategoryDTO[]> {
  return request('/api/ingredient-categories');
}

export function createIngredientCategory(data: CreateIngredientCategoryRequest): Promise<IngredientCategoryDTO> {
  return request('/api/ingredient-categories', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function updateIngredientCategory(code: string, data: UpdateIngredientCategoryRequest): Promise<IngredientCategoryDTO> {
  return request(`/api/ingredient-categories/${code}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

export function deleteIngredientCategory(code: string): Promise<void> {
  return request(`/api/ingredient-categories/${code}`, {
    method: 'DELETE',
  });
}
