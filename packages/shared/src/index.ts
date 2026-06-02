export type AppRole = 'parent' | 'child';

export type FamilyMemberSummary = {
  id: number;
  name: string;
  role: AppRole;
  age: number;
};

export type FamilyMember = {
  id: number;
  familyId: number;
  name: string;
  role: AppRole | 'other';
  gender: 'male' | 'female' | 'other';
  birthday: string | null;
  age?: number | null;
  avatarUrl?: string | null;
  allergens: string[];
  dietaryRestrictions: string[];
  preferences: string[];
};

export type FamilySummary = {
  id: number;
  name: string;
  members: FamilyMemberSummary[];
};

export type SessionUser = {
  id: number;
  username: string;
  displayName: string;
};

export type SessionContext = {
  token: string;
  user: SessionUser;
  currentFamily: FamilySummary;
  currentMember: FamilyMemberSummary;
};

type ThemeDefinition = {
  name: AppRole;
  cssVars: Record<string, string>;
};

export const parentTheme: ThemeDefinition = {
  name: 'parent',
  cssVars: {
    '--bg-primary': '#fff8ef',
    '--bg-secondary': '#ffe8c9',
    '--text-primary': '#4b2e2b',
    '--text-secondary': '#8f6a4a',
    '--accent-primary': '#ff9f43',
    '--card-soft': '#fff4e4',
  },
};

export const childTheme: ThemeDefinition = {
  name: 'child',
  cssVars: {
    '--bg-primary': '#fef4ff',
    '--bg-secondary': '#dff4ff',
    '--text-primary': '#4a3568',
    '--text-secondary': '#7d6c99',
    '--accent-primary': '#7c5cff',
    '--card-soft': '#f1ebff',
  },
};

export const getThemeByRole = (role: AppRole): ThemeDefinition =>
  role === 'parent' ? parentTheme : childTheme;

export type IngredientCategoryDTO = {
  id: number;
  familyId: number;
  name: string;
  code: string;
  parentCode: string | null;
  sortOrder: number;
  icon: string;
  color: string;
  aliases: string[];
};

export type CreateIngredientCategoryRequest = {
  name: string;
  code: string;
  parentCode?: string | null;
  sortOrder?: number;
  icon?: string;
  color?: string;
  aliases?: string[];
};

export type UpdateIngredientCategoryRequest = Partial<CreateIngredientCategoryRequest>;

export type Ingredient = {
  id: number;
  familyId: number;
  name: string;
  categoryCode: string;
  unit: string;
  density: number;
  imageUrl: string | null;
  aliases: string[];
  tags: string[];
};

export type CreateIngredientRequest = {
  name: string;
  categoryCode: string;
  unit: string;
  density?: number;
  imageUrl?: string | null;
  aliases?: string[];
  tags?: string[];
};

export type UpdateIngredientRequest = Partial<CreateIngredientRequest>;
