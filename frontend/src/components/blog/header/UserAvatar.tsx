import { User } from 'lucide-react';
import { UserAvatarProps } from './types';

const UserAvatar = ({ user, onClick }: UserAvatarProps) => (
  <button
    onClick={onClick}
    className="mx-2 flex h-10 w-10 items-center justify-center overflow-hidden rounded-full border-2 border-gray-200 focus:outline-none focus:ring-2 focus:ring-indigo-500"
  >
    {user?.profileImage ? (
      <img src={user.profileImage} alt={user.name} className="h-full w-full object-cover" />
    ) : (
      <div className="flex h-full w-full items-center justify-center bg-gray-200">
        <User size={24} className="text-gray-600" />
      </div>
    )}
  </button>
);

export default UserAvatar;
