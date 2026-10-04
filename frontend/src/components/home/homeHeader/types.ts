import { User } from '@/types/api/user';

export interface ProfileDropdownProps {
  user: User;
  isOpen: boolean;
  onClose: () => void;
  onLogout: () => void;
}

export interface SubscribeModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export interface UserAvatarProps {
  user: User | null;
  size?: 'sm' | 'md';
}
