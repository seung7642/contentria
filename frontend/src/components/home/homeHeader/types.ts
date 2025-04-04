export interface User {
  id?: string;
  name: string;
  email: string;
  profileImage?: string;
}

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
  onClick: () => void;
}
