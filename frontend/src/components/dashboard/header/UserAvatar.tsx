import Image from 'next/image';
import { UserIcon } from 'lucide-react';
import { User } from '@/types/api/user';

interface UserAvatarProps {
  user: User | null | undefined;
  size?: number;
}

const UserAvatar = ({ user, size = 24 }: UserAvatarProps) => {
  const iconSize = size * 0.6;

  return (
    <>
      {user?.profileImage ? (
        <Image
          src={user.profileImage}
          alt={user.username || 'User Avatar'}
          fill
          sizes={`${size}px`}
          className="rounded-full object-cover"
        />
      ) : (
        <div className="flex h-full w-full items-center justify-center rounded-full bg-gray-200">
          <UserIcon size={iconSize} className="text-gray-600" />
        </div>
      )}
    </>
  );
};

export default UserAvatar;
