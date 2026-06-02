import type { Ingredient, CreateIngredientRequest, UpdateIngredientRequest } from '@baby-breakfast/shared';
import { request } from '../api';

export function getIngredients(category?: string): Promise<Ingredient[]> {
  const url = category ? `/api/ingredients?category=${category}` : '/api/ingredients';
  return request(url);
}

export function createIngredient(data: CreateIngredientRequest): Promise<Ingredient> {
  return request('/api/ingredients', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function updateIngredient(id: number, data: UpdateIngredientRequest): Promise<Ingredient> {
  return request(`/api/ingredients/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

export function deleteIngredient(id: number): Promise<void> {
  return request(`/api/ingredients/${id}`, {
    method: 'DELETE',
  });
}
