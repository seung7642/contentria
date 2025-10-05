import React, { forwardRef } from 'react';

type InputWithAddonProps = {
  id: string;
  addon: string;
  errorMessage?: string;
} & Omit<React.InputHTMLAttributes<HTMLInputElement>, 'id'>;

const InputWithAddon = forwardRef<HTMLInputElement, InputWithAddonProps>(
  ({ id, addon, errorMessage, ...props }, ref) => {
    return (
      <div className="text-left">
        <div className="flex rounded-lg shadow-sm">
          {/* Addon 부분: 항상 비활성 스타일 유지 */}
          <span className="inline-flex select-none items-center rounded-l-lg border border-r-0 border-gray-300 bg-gray-50 px-4 text-gray-500 sm:text-lg">
            {addon}
          </span>
          <input
            ref={ref}
            id={id}
            className={`w-full flex-1 appearance-none rounded-none rounded-r-lg border bg-white py-3 pl-3 pr-4 text-gray-900 transition-colors placeholder:text-gray-400 focus:z-10 focus:outline-none sm:text-lg ${
              errorMessage
                ? 'border-red-500 ring-1 ring-red-500 focus:border-red-500 focus:ring-red-500'
                : 'border-gray-300 focus:border-indigo-600 focus:ring-1 focus:ring-indigo-600'
            } `}
            {...props}
          />
        </div>
        {errorMessage && <p className="mt-2 text-sm font-medium text-red-600">{errorMessage}</p>}
      </div>
    );
  }
);

InputWithAddon.displayName = 'InputWithAddon';

export default InputWithAddon;
