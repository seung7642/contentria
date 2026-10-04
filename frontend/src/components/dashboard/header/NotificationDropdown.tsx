import Link from 'next/link';

interface Notification {
  id: number;
  text: string;
  time: string;
  link?: string;
}

interface NotificationDropdownProps {
  notifications: Notification[];
}

const NotificationDropdown = ({ notifications }: NotificationDropdownProps) => {
  return (
    <div className="absolute right-0 z-20 mt-2 w-80 rounded-lg border bg-white shadow-lg">
      <div className="border-b px-4 py-2">
        <h3 className="font-medium">알림</h3>
      </div>
      <div className="max-h-96 overflow-y-auto">
        {notifications.length > 0 ? (
          notifications.map((notification) => (
            <div key={notification.id} className="border-b px-4 py-3 hover:bg-gray-50">
              <p className="text-sm">{notification.text}</p>
              <p className="text-xs text-gray-500">{notification.time}</p>
            </div>
          ))
        ) : (
          <p className="px-4 py-3 text-sm text-gray-500">새 알림이 없습니다.</p>
        )}
      </div>
      {notifications.length > 0 && (
        <div className="border-t px-4 py-2 text-center">
          <Link
            href="/dashboard/notifications"
            className="text-sm text-indigo-600 hover:text-indigo-800"
          >
            모든 알림 보기
          </Link>
        </div>
      )}
    </div>
  );
};

export default NotificationDropdown;
