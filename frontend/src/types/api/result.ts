import { ApiError } from './errors';

type Success<T> = { success: true; data: T };
type Failure = { success: false; error: ApiError };

export type ApiResult<T> = Success<T> | Failure;
