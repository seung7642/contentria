import React from 'react';
import { TrendingUp } from 'lucide-react';

interface StatCardProps {
  icon: React.ReactNode;
  title: string;
  value: number;
  trend: string;
  trendUp: boolean;
}

const StatCard: React.FC<StatCardProps> = ({ icon, title, value, trend, trendUp }) => {
  return (
    <div className="rounded-lg bg-white p-5 shadow-sm">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm font-medium text-gray-500">{title}</p>
          <p className="mt-1 text-2xl font-bold text-gray-800">{value}</p>
        </div>
        <div className="rounded-full bg-indigo-50 p-3 text-indigo-600">{icon}</div>
      </div>
      <div
        className={`mt-3 flex items-center text-sm ${trendUp ? 'text-green-600' : 'text-red-600'}`}
      >
        <TrendingUp size={16} className="mr-1" />
        <span>{trend}</span>
      </div>
    </div>
  );
};

export default StatCard;
